# Full Stack Job Scheduler

This project is a simplified job scheduler application with an Angular frontend and a Java Spring Boot backend. It visualizes job statuses and allows users to submit new jobs. The scheduler prioritizes jobs using the Shortest Job First (SJF) algorithm, with real-time UI updates achieved using WebSockets.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [WebSocket Configuration](#websocket-configuration)

## Features

- Submit new jobs with a name and duration.
- Display the list of jobs with their statuses (pending, running, completed).
- Real-time updates using WebSockets for job status changes.
- SJF scheduling algorithm to prioritize jobs.

## Technologies

- Angular 13
- Java Spring Boot
- WebSockets
- Angular Material for UI components

## Setup

### Prerequisites

- Node.js (v14 or higher)
- Angular CLI (v13)
- Java Development Kit (JDK 11 or higher)
- Maven

### Backend Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd job-scheduler-backend
   ```

2. Build the backend:
   ```bash
   mvn clean install
   ```

3. Run the backend:
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd job-scheduler-frontend
   ```

2. Install the dependencies:
   ```bash
   npm install
   ```

3. Run the frontend:
   ```bash
   ng serve
   ```

## Running the Application

### Backend

The backend runs on `http://localhost:8080`. Ensure it is running before starting the frontend.

### Frontend

The frontend runs on `http://localhost:4200`. Open this URL in your web browser to access the application.

## API Endpoints

### POST /api/jobs
Submit a new job.
- Request Body:
  ```json
  {
    "name": "Job Name",
    "duration": 1000
  }
  ```

### GET /api/jobs
Retrieve the current list of jobs.
- Response Body:
  ```json
  [
    {
      "name": "Job Name",
      "duration": 1000,
      "status": "pending"
    }
  ]
  ```

## WebSocket Configuration

### WebSocket Endpoint

The WebSocket server runs on `ws://localhost:8080/ws`.

### WebSocket Messages

- **Job Added**
  ```json
  {
    "name": "Job Name",
    "duration": 1000,
    "status": "pending"
  }
  ```

- **Job Status Updated**
  ```json
  {
    "name": "Job Name",
    "duration": 1000,
    "status": "running/completed"
  }
  ```

## Troubleshooting

### Common Issues

1. **WebSocket Not Connecting**:
    - Ensure the backend is running.
    - Check if the WebSocket endpoint (`ws://localhost:8080/ws`) is correct.

2. **CORS Issues**:
    - Ensure the backend has CORS configured to allow requests from the frontend's origin.

3. **Frontend Not Updating in Real-Time**:
    - Ensure WebSocket messages are correctly sent from the backend.
    - Check the console for any errors in the WebSocket connection.
