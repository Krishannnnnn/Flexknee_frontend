
param(
    [string]$SourceFile,
    [string]$ResDir
)

Add-Type -AssemblyName System.Drawing

$sizes = @{
    "mipmap-mdpi"    = 48
    "mipmap-hdpi"    = 72
    "mipmap-xhdpi"   = 96
    "mipmap-xxhdpi"  = 144
    "mipmap-xxxhdpi" = 192
}

$srcImage = [System.Drawing.Image]::FromFile($SourceFile)

foreach ($key in $sizes.Keys) {
    $size = $sizes[$key]
    $destDir = Join-Path $ResDir $key
    
    if (-not (Test-Path $destDir)) {
        New-Item -ItemType Directory -Path $destDir | Out-Null
    }

    # Define paths
    $destPathLauncher = Join-Path $destDir "ic_launcher.png"
    $destPathRound = Join-Path $destDir "ic_launcher_round.png"
    
    # Resize Logic
    $bmp = New-Object System.Drawing.Bitmap $size, $size
    $graph = [System.Drawing.Graphics]::FromImage($bmp)
    $graph.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graph.DrawImage($srcImage, 0, 0, $size, $size)
    
    # Save as PNG
    $bmp.Save($destPathLauncher, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Save($destPathRound, [System.Drawing.Imaging.ImageFormat]::Png) # Using same for round for now
    
    $graph.Dispose()
    $bmp.Dispose()
    
    Write-Host "Created icons in $key"

    # Remove conflicting WebP files if they exist
    $webpLauncher = Join-Path $destDir "ic_launcher.webp"
    $webpRound = Join-Path $destDir "ic_launcher_round.webp"
    
    if (Test-Path $webpLauncher) { Remove-Item $webpLauncher; Write-Host "Defined webp deleted: $webpLauncher" }
    if (Test-Path $webpRound) { Remove-Item $webpRound; Write-Host "Defined webp deleted: $webpRound" }
}

$srcImage.Dispose()
