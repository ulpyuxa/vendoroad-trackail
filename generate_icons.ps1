Add-Type -AssemblyName System.Drawing

$srcPath = "C:\Users\Simon\.gemini\antigravity\brain\6cfc444b-d264-41c4-85a8-3d4ba7e4fb61\trackail_icon_orange_1774407375540.png"
$resFolder = "c:\Users\Simon\trackail\app\src\main\res"

$sizes = @{
    "mdpi" = 48
    "hdpi" = 72
    "xhdpi" = 96
    "xxhdpi" = 144
    "xxxhdpi" = 192
}

try {
    $srcImg = [System.Drawing.Image]::FromFile($srcPath)
    
    foreach ($entry in $sizes.GetEnumerator()) {
        $dpi = $entry.Key
        $size = $entry.Value
        
        $destFolder = "$resFolder\mipmap-$dpi"
        if (-not (Test-Path $destFolder)) {
            New-Item -ItemType Directory -Force -Path $destFolder | Out-Null
        }
        
        $bmp = New-Object System.Drawing.Bitmap $size, $size
        $graphics = [System.Drawing.Graphics]::FromImage($bmp)
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.DrawImage($srcImg, 0, 0, $size, $size)
        
        $bmp.Save("$destFolder\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
        $bmp.Save("$destFolder\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)
        
        $graphics.Dispose()
        $bmp.Dispose()
        Write-Host "Generated $dpi (${size}x${size})"
    }
    
    $srcImg.Dispose()
    Write-Host "Icons generated successfully!"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
