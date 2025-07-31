# User Project Testcase Generation

You are tasked with generating backend API test cases based on the following requirements.

## Overview
- The backend system dynamically creates temporary tables at application startup and drops them on shutdown.
- All test cases are intended to validate API functionality against expected behavior using live API requests.
- The output must be in CSV format.

## How Test Methodology
- For each test case, send a real HTTP request to the API endpoint.
- Capture the actual response and compare it with the expected result.
- The table involved should be created as part of the test setup and removed afterward, as it is not persistent.

## Generation Rules
- Each test case must include the following columns in the CSV:
    - Test Case ID: Format must be `REQ03-TC-{{API Number}}-{{Test Case Number per API}}`
    - Test API Endpoint: The full HTTP endpoint path
    - Test Case Name: A brief description of the test scenario
    - Verification Point: The key value(s) or logic to verify (e.g., status code, returned JSON field)
    - Expected Result: The expected output or behavior from the API
- Test cases must be created for all API endpoints and validated for all cases.

Please generate a table of multiple test cases according to this structure, covering various edge cases and normal cases.
