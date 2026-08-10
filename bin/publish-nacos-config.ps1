param(
    [string]$Server = "http://127.0.0.1:8848",
    [string]$Group = "DEFAULT_GROUP",
    [string]$Username = "nacos",
    [string]$Password = "nacos",
    [string]$ConfigDir = "$PSScriptRoot\..\docker\nacos\config"
)

$ErrorActionPreference = "Stop"

function Join-NacosUrl([string]$Base, [string]$Path) {
    return $Base.TrimEnd("/") + $Path
}

function Get-NacosAccessToken {
    $loginUrl = Join-NacosUrl $Server "/nacos/v1/auth/users/login"
    for ($i = 1; $i -le 60; $i++) {
        try {
            $response = Invoke-RestMethod `
                -Method Post `
                -Uri $loginUrl `
                -ContentType "application/x-www-form-urlencoded" `
                -Body @{ username = $Username; password = $Password }
            if ($response.accessToken) {
                return $response.accessToken
            }
        } catch {
            Start-Sleep -Seconds 2
        }
    }
    throw "Nacos login failed at $loginUrl"
}

if (-not (Test-Path -LiteralPath $ConfigDir)) {
    throw "Config directory not found: $ConfigDir"
}

$token = Get-NacosAccessToken
$publishUrl = Join-NacosUrl $Server "/nacos/v1/cs/configs"

Get-ChildItem -LiteralPath $ConfigDir -Filter "*.yaml" | Sort-Object Name | ForEach-Object {
    $content = Get-Content -LiteralPath $_.FullName -Raw
    $body = @{
        dataId = $_.Name
        group = $Group
        type = "yaml"
        content = $content
        accessToken = $token
    }

    $result = Invoke-RestMethod `
        -Method Post `
        -Uri $publishUrl `
        -ContentType "application/x-www-form-urlencoded" `
        -Body $body

    if ($result -ne $true -and $result -ne "true") {
        throw "Failed to publish $($_.Name): $result"
    }

    Write-Host "Published $($_.Name) to Nacos group $Group"
}
