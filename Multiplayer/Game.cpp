#include "Game.h"




SDL_Renderer* Game::renderer = nullptr;
SDL_Event Game::playerInputEvent;



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

		for (int i = 0; i < args.size(); i++) {
			std::string playerID = args[i];
			int x = std::stoi(args[i + 1]);
			int y = std::stoi(args[i + 2]);

			playerPositions[playerID] = { x,y };
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
			msg = isDown ? "MOVE_UP_DOWN" : "MOVE_UP_UP";
		}
		if (!msg.empty()) {
			send(msg);
		}
	}

}


void Game::update() {
	render();
}

void Game::render() {
	SDL_SetRenderDrawColor(renderer, 255, 0, 0, 255);
	SDL_RenderClear(renderer);
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
}
void Game::GameLoop() {
	
}

Game::Game() {

	startSDL();
	welcomeScreen();
	//createGameObjects();
}
