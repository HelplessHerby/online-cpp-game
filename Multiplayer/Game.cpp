#include "Game.h"




SDL_Renderer* Game::renderer = nullptr;
SDL_Event Game::playerInputEvent;

Player* localPlayer;
Player* remotePlayer;


void Game::send(std::string message) {
	if (!message.empty()) {
		message += "\n";
		messages.push_back(message);
	}

}

void Game::on_receive(std::string cmd, std::vector<std::string>& args) {
	if (cmd == "GAME_DATA") {

		//resets player positions
		playerPositions.clear();

		for (size_t i = 0; i + 2 < args.size(); i += 3) {
			std::string playerID = args[i];
			float x = std::stof(args[i + 1]);
			float y = std::stof(args[i + 2]);

			if (playerID == localplayerID) {
				localPlayer->setPos(x, y);
			}
			else {
				remotePlayer->setPos(x, y);
			}

			playerPositions[playerID] = { x, y };

		}
	}
	else if (cmd == "ASSIGN_ID") {
		if (!args.empty()) {
			localplayerID = args[0];
			std::cout << "[Client] Assigned Player ID: " << localplayerID << std::endl;
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

		localPlayer->handleInput(msg);

	}

}


void Game::update(float deltaTime) {

	localPlayer->update(deltaTime);
	remotePlayer->update(deltaTime);
}

void Game::render() {
	SDL_SetRenderDrawColor(renderer, 30, 30, 30, 255);
	SDL_RenderClear(renderer);

	localPlayer->render(renderer);
	remotePlayer->render(renderer);

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

	localPlayer = new Player(1, 200, 200,renderer);
	remotePlayer = new Player(2, 400, 200, renderer);

}

void Game::sendPlayerPos() {
	float x, y;
	localPlayer->getPos(x, y);
	float rot = localPlayer->getRot();

	std::string msg = "Player: ";
}

void Game::GameLoop() {
	
}

Game::Game() {

	startSDL();
	welcomeScreen();
}
