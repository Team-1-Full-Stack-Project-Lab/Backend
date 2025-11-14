# Quick Test Commands

## Run Trip Service Tests

```bash
# Navigate to backend directory
cd s:/softserve/FullStack/Backend

# Run all tests (includes all 23 Trip tests)
./gradlew test

# Run Trip tests specifically
./gradlew test --tests TripServiceTest

# Run with clean build
./gradlew clean test

# Run with detailed output
./gradlew test --info
```

**Note:** ApplicationTests (integration test) is disabled - no database needed!

## View Test Results

```bash
# Open HTML report (Windows)
start build/reports/tests/test/index.html

# Or navigate to:
# s:/softserve/FullStack/Backend/build/reports/tests/test/index.html
```

## Expected Results

-   ✅ 23 tests
-   ✅ 0 failures
-   ✅ 100% success rate
-   ⏱️ Duration: ~1.5 seconds

## What Was Tested

### TripService Methods:

1. **createTrip()** - 8 tests
2. **getUserTrips()** - 3 tests
3. **deleteTrip()** - 3 tests
4. **updateTrip()** - 9 tests

---

See `TESTING_GUIDE.md` for full documentation.
