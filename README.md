# Welcome to the Calculator App Project!

We're excited to have you join our team. This document aims to provide you with all the necessary information to get you started on developing the Calculator app with currency conversion functionality. Our app is designed for Android, leveraging a Flask backend for currency conversion data.
## Project Overview
The Calculator app is a minimalist Android application that allows users to perform basic arithmetic operations and convert currencies using live exchange rates. The project is open-sourced, inviting contributions from developers around the world.
## Key Features:
Basic arithmetic operations (+, -, *, /, %)
Currency conversion with 4 customizable pinned currency pairs
A home screen widget for quick calculations and conversions
## Technical Architecture
- Android App:
  - Language: Kotlin
  - Architecture: Model-View-ViewModel (MVVM)
  - Key Libraries: Retrofit (for network requests), ViewModel, LiveData
- Backend Server:
  - Language: Python with Flask framework
  - Database: Redis (for caching currency rates)
  - External API: Open Exchange Rates for fetching live currency data
## Development Environment Setup
Software Installation:
- Install Android Studio for Android app development.
- Install Python, Flask, and Redis for backend development.
- Ensure Git is installed for version control.
### Project Setup:
- Clone the project repository from GitHub.
- For Android development, open the project in Android Studio and sync Gradle.
- For backend development, set up a virtual environment in Python, install dependencies from requirements.txt, and ensure Redis is running locally.
### Running the Project:
- Run the Android app through Android Studio using an emulator or physical device.
- Start the Flask server locally to handle currency conversion requests.
## Coding Standards and Best Practices
- Adhere to the Kotlin Coding Conventions for Android app development.
- Follow PEP 8 -- Style Guide for Python Code for backend development.
- Write clear, concise commit messages and make pull requests for new features or bug fixes.
- Ensure code is well-commented, and add unit tests for new functionality.
## Collaboration and Communication
- Project Management: We use GitHub Issues and Projects to track tasks, bugs, and features.
- Communication Channels: Slack for team communication and Google Meet for weekly sync-up meetings.
- Documentation: Access project documentation, including API specs and design docs, on our GitHub wiki.
## Security and Privacy Practices
- Handle user data with care, ensuring anonymization and privacy.
- Follow secure coding practices to protect against common vulnerabilities.
## Additional Resources
- FAQ: Find answers to common questions on the project wiki.
- Contact List: Reach out to team leads or domain experts as listed in the contact directory on the wiki.
## Calculator App Currency Conversion API Documentation
### Overview
This API provides real-time currency conversion rates for the Calculator app. It allows the Android app to fetch the latest exchange rates between different currencies.
### Base URL
```https://<your-backend-domain>/api```
<br>
Replace <your-backend-domain> with the actual domain name where the Flask backend is hosted.
### Authentication
Currently, no authentication is required to access the API endpoints.

(Note: Consider implementing API keys or OAuth for production environments to secure access.)
### Rate Limiting
Requests are limited to 60 per hour per IP address to prevent abuse and ensure service reliability.
### Endpoints
Fetch Currency Conversion Rate
- URL: /conversion
- Method: GET
- URL Params:
- Required: from=[currencyCode], to=[currencyCode]
- Example: /conversion?from=USD&to=EUR
- Success Response:
  - Code: 200
  - Content:
    {
 "from": "USD",
 "to": "EUR",
 "rate": 0.85126,
 "timestamp": "2023-03-01T12:00:00Z"
}

- Error Response:
- Code: 400 BAD REQUEST
- Content: {"error": "Invalid currency code"}
- Sample Call:
RetrofitInstance.api.getConversionRate("USD", "EUR")
Notes:
Rates are updated hourly.
Currency codes must be in ISO 4217 format.
## Sample Usage
To fetch the conversion rate from USD to EUR, make a GET request to /conversion?from=USD&to=EUR. The response will include the conversion rate and the timestamp of the last update.

## Error Handling
All API endpoints return standard HTTP status codes to indicate the success or failure of a request. In case of an error, a JSON object containing an error description will be provided.
Rate Updates
Currency conversion rates are fetched from the Open Exchange Rates API and cached for one hour to reduce the number of external API calls and improve response times.
Security Considerations
While the current version of the API does not require authentication.

This documentation provides a basic outline for the currency conversion API between the Flask backend and the Android app. It's designed to be expanded as new features are added or changes are made to the API.
