ChessComp
ChessComp is a comprehensive chess tournament platform designed for chess enthusiasts of all skill levels. Whether you're a grandmaster or a beginner, 
ChessComp offers exciting tournaments that challenge your strategic thinking and push your skills to new heights. Engage with players from around the globe, 
compete in real-time matches, and rise through the ranks to claim your place among the champions.

Features
Real-time Tournaments: Participate in live chess tournaments and compete against players worldwide.
User Profiles: Track your progress and view your personal stats.
Leaderboards: See how you rank against other players.
WebSocket Integration: Real-time updates and notifications.
Responsive Design: Optimized for both desktop and mobile devices.
Technologies Used
Frontend
React: A JavaScript library for building user interfaces.
Vite: A build tool that provides a faster and leaner development experience for modern web projects.
CSS: Custom styles for a polished look and feel.
Backend
Spring Boot: A framework for building production-ready applications.
WebSocket: For real-time communication between the server and clients.
Spring Security: For securing the application.
JPA: For database interactions.

Getting Started
Prerequisites
Node.js: Ensure you have Node.js installed.
Java: Ensure you have Java installed.
Maven: Ensure you have Maven installed.
Installation
Clone the repository:

git clone https://github.com/cledwynchan2023/CS203_Proj.git
cd CS203_Proj

Install frontend dependencies:
cd fullstack-proj-frontend
npm install

Install backend dependencies:
cd ../fullstack-backend-proj2
./mvnw install

Running the Application
Start the backend server:
cd fullstack-backend-proj2
./mvnw spring-boot:run

Start the frontend server:
cd ../fullstack-proj-frontend
npm run dev

Project Structure
ChessComp/
├── fullstack-backend-proj2/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── package.json
├── fullstack-proj-frontend/
│   ├── src/
│   ├── public/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
└── .gitignore

Contributing
We welcome contributions! Please read our Contributing Guidelines for more details.

License
This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

Acknowledgements
Special thanks to the open-source community for their invaluable contributions.
Icons made by Freepik from Flaticon.
Contact
For any inquiries or feedback, please contact us at cledwynchan@gmail.com

Credits to Yu Xuan, Damien, Zern, Shyann!

FIX 1
If main branch does not work locally, use SAFE-BRANCH Branch to view the project locally!
