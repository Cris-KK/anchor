# Simple Audience Service Test Script
# Usage: .\simple-test.ps1

param(
    [string]$BaseUrl = "http://localhost:8091"
)

$headers = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

function Write-TestStep {
    param([string]$Message, [string]$Status = "Info")

    switch ($Status) {
        "Success" { Write-Host "[PASS] $Message" -ForegroundColor Green }
        "Error" { Write-Host "[FAIL] $Message" -ForegroundColor Red }
        "Warning" { Write-Host "[WARN] $Message" -ForegroundColor Yellow }
        default { Write-Host "[INFO] $Message" -ForegroundColor Cyan }
    }
}

function Test-Health {
    Write-Host "`n=== Health Check Test ===" -ForegroundColor Magenta

    try {
        $result = Invoke-RestMethod -Uri "$BaseUrl/health" -Method GET -TimeoutSec 10
        Write-TestStep "Health check successful" "Success"
        Write-Host "Service: $($result.data.service)" -ForegroundColor White
        Write-Host "Status: $($result.data.status)" -ForegroundColor White
        Write-Host "Port: $($result.data.port)" -ForegroundColor White
        return $true
    }
    catch {
        Write-TestStep "Health check failed: $($_.Exception.Message)" "Error"
        return $false
    }
}

function Test-Reward {
    Write-Host "`n=== Basic Reward Test ===" -ForegroundColor Magenta

    $rewardData = @{
        audienceId = "test_audience_001"
        anchorId = "test_anchor_001"
        amount = 100.50
    } | ConvertTo-Json

    try {
        $result = Invoke-RestMethod -Uri "$BaseUrl/audience/reward" -Method POST -Body $rewardData -Headers $headers -TimeoutSec 10
        Write-TestStep "Reward request successful" "Success"
        Write-Host "Message: $($result.message)" -ForegroundColor White

        if ($result.data) {
            Write-Host "Record ID: $($result.data.recordId)" -ForegroundColor White
        }
        return $true
    }
    catch {
        Write-TestStep "Reward request failed: $($_.Exception.Message)" "Error"
        return $false
    }
}

function Test-Top10 {
    Write-Host "`n=== TOP10 Query Test ===" -ForegroundColor Magenta

    try {
        $result = Invoke-RestMethod -Uri "$BaseUrl/audience/reward/top10/test_anchor_001" -Method GET -TimeoutSec 10
        Write-TestStep "TOP10 query successful" "Success"

        if ($result.data -and $result.data.Count -gt 0) {
            Write-Host "Found $($result.data.Count) audiences:" -ForegroundColor White
            for ($i = 0; $i -lt [Math]::Min($result.data.Count, 5); $i++) {
                $audience = $result.data[$i]
                Write-Host "  #$($i+1): $($audience.audienceId) - Amount: $($audience.totalAmount)" -ForegroundColor Gray
            }
        } else {
            Write-TestStep "No audience data found" "Warning"
        }
        return $true
    }
    catch {
        Write-TestStep "TOP10 query failed: $($_.Exception.Message)" "Error"
        return $false
    }
}

function Test-AudienceTag {
    Write-Host "`n=== Audience Tag Test ===" -ForegroundColor Magenta

    try {
        $result = Invoke-RestMethod -Uri "$BaseUrl/audience/test_audience_001/tag" -Method GET -TimeoutSec 5
        Write-TestStep "Tag query successful" "Success"
        Write-Host "Tag: $($result.data)" -ForegroundColor White
        return $true
    }
    catch {
        Write-TestStep "Tag query failed (Analytics service may not be running): $($_.Exception.Message)" "Warning"
        return $false
    }
}

function Test-AudienceStats {
    Write-Host "`n=== Audience Stats Test ===" -ForegroundColor Magenta

    try {
        $result = Invoke-RestMethod -Uri "$BaseUrl/audience/test_audience_001/stats" -Method GET -TimeoutSec 10
        Write-TestStep "Stats query successful" "Success"

        if ($result.data) {
            Write-Host "Stats:" -ForegroundColor White
            $result.data | ConvertTo-Json -Depth 2 | Write-Host -ForegroundColor Gray
        }
        return $true
    }
    catch {
        Write-TestStep "Stats query failed: $($_.Exception.Message)" "Error"
        return $false
    }
}

function Test-EdgeCases {
    Write-Host "`n=== Edge Cases Test ===" -ForegroundColor Magenta

    # Test negative amount
    Write-TestStep "Testing negative amount..." "Info"
    $negativeData = @{
        audienceId = "test_audience"
        anchorId = "test_anchor"
        amount = -100
    } | ConvertTo-Json

    try {
        Invoke-RestMethod -Uri "$BaseUrl/audience/reward" -Method POST -Body $negativeData -Headers $headers -TimeoutSec 10
        Write-TestStep "Negative amount test FAILED - should be rejected" "Warning"
    }
    catch {
        Write-TestStep "Negative amount correctly rejected" "Success"
    }

    # Test empty audience ID
    Write-TestStep "Testing empty audience ID..." "Info"
    $emptyData = @{
        audienceId = ""
        anchorId = "test_anchor"
        amount = 100
    } | ConvertTo-Json

    try {
        Invoke-RestMethod -Uri "$BaseUrl/audience/reward" -Method POST -Body $emptyData -Headers $headers -TimeoutSec 10
        Write-TestStep "Empty ID test FAILED - should be rejected" "Warning"
    }
    catch {
        Write-TestStep "Empty ID correctly rejected" "Success"
    }
}

function Start-Tests {
    Write-Host "=== Audience Service Test Started ===" -ForegroundColor Magenta
    Write-Host "Target: $BaseUrl" -ForegroundColor White
    Write-Host "Time: $(Get-Date)" -ForegroundColor Gray

    $results = @{}

    # Run all tests
    $results["Health"] = Test-Health

    if ($results["Health"]) {
        $results["Reward"] = Test-Reward
        $results["Top10"] = Test-Top10
        $results["Tag"] = Test-AudienceTag
        $results["Stats"] = Test-AudienceStats
        $results["EdgeCases"] = Test-EdgeCases
    } else {
        Write-TestStep "Service not accessible, skipping other tests" "Error"
    }

    # Summary
    Write-Host "`n=== Test Summary ===" -ForegroundColor Magenta

    $passed = 0
    $total = $results.Count

    foreach ($test in $results.GetEnumerator()) {
        $status = if ($test.Value) { "PASS"; $passed++ } else { "FAIL" }
        $color = if ($test.Value) { "Green" } else { "Red" }
        Write-Host "$($test.Key): $status" -ForegroundColor $color
    }

    Write-Host "`nResult: $passed/$total tests passed" -ForegroundColor $(if($passed -eq $total){"Green"}else{"Yellow"})

    if ($passed -eq $total) {
        Write-Host "All tests passed! Service is working correctly!" -ForegroundColor Green
    } else {
        Write-Host "Some tests failed. Please check the service." -ForegroundColor Yellow
    }
}

# Run the tests
Start-Tests