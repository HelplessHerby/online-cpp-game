#include "Game.h"




SDL_Renderer* Game::renderer = nullptr;
SDL_Event Game::playerInputEvent;

Player* players[4];


void Game::send(std::string message) {
	if (!message.empty()) {
		message += "\n";
		messages.push_back(message);
	}

}

void Game::on_receive(std::string cmd, std::vector<std::string>& args) {
	if (cmd == "ASSIGN_ID") {
			
			std::string localPlayerStr = args[0];
			size_t pos = localPlayerStr.find(":");
			if (pos == std::string::npos) {
				std::cerr << "[Client] ERROR: ASSIGN_ID missing colon in: "
					<< localPlayerStr << std::endl;
				return;
			}
			localplayerID = std::stoi(localPlayerStr.substr(pos + 1));

			
			std::cout << "[Client] Assigned Player ID: " << localplayerID << std::endl;
			
	}
	else if (cmd == "GAME_DATA") {

		//resets player positions
		//playerPositions.clear();
		std::string localPlayerStr = args[0];
		size_t pos = localPlayerStr.find(":");
		if (pos == std::string::npos) {
			std::cerr << "[Client] ERROR: ASSIGN_ID missing colon in: "
				<< localPlayerStr << std::endl;
			return;
		}
		int ID = std::stoi(localPlayerStr.substr(pos + 1));

		for (size_t i = 0; i + 3 < args.size(); i += 4) {

			float x = std::stof(args[i + 1]);
			float y = std::stof(args[i + 2]);
			float rot = std::stof(args[i + 3]);
			
			std::cout << "id: " << localplayerID << " x: " << x << " y: " << y << " rot: " << rot << "\n";
			if (ID >= 0 && ID < 4 && players[ID] != nullptr) {
				players[ID]->setPos(x, y, rot);

			}


			playerPositions[args[i]] = { x, y };

		}
	}

	else {
		std::cout << "Received: " << cmd << std::endl;
	}

}

void Game::input(SDL_Event& event) {

	SDL_Keycode key = event.key.keysym.sym;

	bool isDown = event.type == SDL_KEYDOWN;

	if (keyDown[key] != isDown) {
		keyDown[key] = isDown;

		std::string msg;

		switch (key) {
		case SDLK_w:
			msg = isDown ? "MOVE_UP_DOWN" : "MOVE_UP_UP"; break;
		case SDLK_s:
			msg = isDown ? "MOVE_DOWN_DOWN" : "MOVE_DOWN_UP"; break;
		case SDLK_a:
			msg = isDown ? "TURN_LEFT_DOWN" : "TURN_LEFT_UP"; break;
		case SDLK_d:
			msg = isDown ? "TURN_RIGHT_DOWN" : "TURN_RIGHT_UP"; break;
		}
		if (!msg.empty()) {
			send(msg);
		}

		players[0]->handleInput(msg);

	}

}


void Game::update(float deltaTime) {

}

void Game::render() {
	SDL_SetRenderDrawColor(renderer, 30, 30, 30, 255);
	SDL_RenderClear(renderer);

	players[0]->render(renderer);
	players[1]->render(renderer);
	players[2]->render(renderer);
	players[3]->render(renderer);


	SDL_RenderPresent(renderer);
}



void Game::startSDL() {
	if (SDL_Init(SDL_INIT_EVERYTHING) == 0) {


		gameWindow = SDL_CreateWindow(GAME_TITLE, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED, SCREEN_WIDTH, SCREEN_HEIGHT, 0);

		renderer = SDL_CreateRenderer(gameWindow, -1, 0);
		gameRunning = true;
		send("Game Created");
	}
	else {
		gameRunning = false;
	}
}

void Game::Close() {
	
	gameRunning = false;
	SDL_DestroyRenderer(renderer);
	SDL_DestroyWindow(gameWindow);
} 


void Game::welcomeScreen() {
	send("Game Welcome");
	gameRunning = true;

	players[0] = new Player(1, 200, 200, renderer);
	players[1] = new Player(2, 400, 200, renderer);
	players[2] = new Player(3, 400, 400, renderer);
	players[3] = new Player(4, 600, 600, renderer);

}

void Game::sendPlayerPos() {
	float x, y;
	players[0]->getPos(x, y);
	float rot = players[1]->getRot();

	std::string msg = "Player: ";
}

void Game::GameLoop() {
	
}

Game::Game() {

	startSDL();
	welcomeScreen();
}
