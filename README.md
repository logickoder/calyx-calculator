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



# Calculator Backend Setup Guide

Welcome to the Calculator Backend setup guide! This document will walk you through the steps needed to get the backend server running on your local machine.

## Prerequisites

Before you begin, make sure you have Python installed on your system. This project requires Python 3.6 or newer.

## Getting Started

1. **Clone the Repository**

   First, clone the project repository to your local machine. You can do this by running the following command in your terminal:

   ```bash
   git clone <repository_url>
   ```

   Replace `<repository_url>` with the actual URL of the project repository.

2. **Navigate to the Backend Directory**

   Change into the `CalculatorBackend` directory within the cloned project:

   ```bash
   cd path/to/CalculatorBackend
   ```

   Ensure you replace `path/to/CalculatorBackend` with the actual path to the `CalculatorBackend` directory on your system.

3. **Create a Python Virtual Environment**

   It's recommended to create a virtual environment to manage the project's dependencies. Run the following command to create a virtual environment named `venv`:

   ```bash
   python -m venv venv
   ```

4. **Activate the Virtual Environment**

   Before installing dependencies, you need to activate the virtual environment. Use the appropriate command based on your operating system:

   - **On Windows:**

     ```cmd
     venv\Scripts\activate
     ```

   - **On macOS and Linux:**

     ```bash
     source venv/bin/activate
     ```

   You should now see `(venv)` at the beginning of your command line prompt, indicating that the virtual environment is activated.

5. **Install Project Dependencies**

   With the virtual environment activated, install the project dependencies using the following command:

   ```bash
   pip install -r requirements.txt
   ```

   This command reads the `requirements.txt` file and installs all the required Python packages.

6. **Set Environment Variables**

   Before running the application, you need to set the necessary environment variables. Rename the `.env.example` file to `.env` and update it with your actual configuration values:

   - **On Windows:**

     ```cmd
     copy .env.example .env
     ```

   - **On macOS and Linux:**
      ```bash
      cp .env.example .env
      ```

   Then, open the `.env` file in your favorite text editor and set the values for the environment variables defined within.

7. **Run the Flask Application**

   Finally, start the Flask application by running:

   ```bash
   python run.py
   ```

   You should see output indicating that the server is running, and it will tell you on which address and port it's accessible.

Congratulations! You've successfully set up and run the Flask backend locally. You can now access the API endpoints from your web browser or API client at whichever port you configured.

## Next Steps

- Explore the API endpoints defined in `routes.py`.
- Deploying your Flask application to a cloud service like Heroku for wider accessibility.

## Troubleshooting

If you encounter any issues during setup, ensure that:

- You're running the commands in the project's root directory.
- The virtual environment is activated when installing dependencies and running the application.
- All environment variables in the `.env` file are correctly set.

For more specific errors, consulting the Flask and Python documentation or searching for the error message online can provide solutions.