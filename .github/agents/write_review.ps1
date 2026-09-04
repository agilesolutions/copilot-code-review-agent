<#
write_review.ps1

Sanitize a git branch name to a safe filename and write provided Markdown content into reviews\<sanitized-branch>.md.

Usage examples:
# From repo root, read content from stdin:
#   Get-Content report.md -Raw | .\.github\agents\write_review.ps1
# Provide content as parameter:
#   .\.github\agents\write_review.ps1 -Content (Get-Content report.md -Raw)
# Provide explicit branch name:
#   .\.github\agents\write_review.ps1 -BranchName "feature/awesome-thing" -Content "# Review..."
#
# Behavior:
# - If -BranchName not given, uses `git rev-parse --abbrev-ref HEAD`.
# - Sanitization rules follow the agent spec: replace separators and unsafe chars with '-', collapse dashes,
#   trim unsafe leading/trailing chars, prevent path traversal, and append .md.
# - Creates the reviews directory at repo root if missing and overwrites existing report file.
#
param(
    [string]$BranchName,
    [string]$Content,
    [string]$OutputDir = "reviews"
)

function Get-RepoRoot {
    $root = git rev-parse --show-toplevel 2>$null
    if ([string]::IsNullOrWhiteSpace($root)) { return (Get-Location).ProviderPath }
    return $root.Trim()
}

# Read content from stdin if not provided
if (-not $Content) {
    $stdin = [Console]::In.ReadToEnd()
    if (-not [string]::IsNullOrEmpty($stdin)) { $Content = $stdin }
}

# Determine branch name if not provided
if (-not $BranchName) {
    try {
        $BranchName = (git rev-parse --abbrev-ref HEAD 2>$null).Trim()
    } catch {
        $BranchName = "unknown-branch"
    }
}

if (-not $BranchName) { $BranchName = "branch" }

# Sanitization per rules
$name = $BranchName
# Replace slashes and backslashes with -
$name = $name -replace '[\\/]', '-'
# Replace whitespace with -
$name = $name -replace '\s+', '-'
# Replace characters outside [A-Za-z0-9._-] with -
$name = $name -replace '[^A-Za-z0-9._-]', '-'
# Collapse consecutive -
$name = $name -replace '-{2,}', '-'
# Prevent .. sequences
$name = $name -replace '\.\.+', '-'
# Trim leading/trailing -, ., _
$name = $name.Trim('-','_','.')
# If empty after cleaning, fallback
if (-not $name) { $name = 'branch' }

# Ensure no directory separators remain
$name = $name -replace '[\\/]', '-'

$filename = "$name.md"

$repoRoot = Get-RepoRoot
$outDirPath = Join-Path -Path $repoRoot -ChildPath $OutputDir
if (-not (Test-Path $outDirPath)) { New-Item -ItemType Directory -Path $outDirPath | Out-Null }

$filePath = Join-Path -Path $outDirPath -ChildPath $filename

if (-not $Content) {
    Write-Error "No content provided. Provide -Content or pipe content to the script via stdin. Example: Get-Content report.md -Raw | .\\.github\\agents\\write_review.ps1"
    exit 2
}

# Write the content (overwrite)
Set-Content -Path $filePath -Value $Content -Encoding UTF8
Write-Output "WROTE: $filePath"
