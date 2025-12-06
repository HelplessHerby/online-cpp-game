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
			if (localplayerID <= 4) {
				players[localplayerID] = new Player(localplayerID, 200 * localplayerID, 200 * localplayerID, renderer);
				std::cout << "[Client] Assigned Player ID: " << localplayerID << std::endl;
			}
			else {
				std::cout << "Too Many Players";
			}
			
			
	}
	else if (cmd == "GAME_DATA")
	{
		for (size_t i = 0; i + 3 < args.size(); i += 4)
		{
			std::string idStr = args[i];
			size_t pos = idStr.find(":");
			if (pos == std::string::npos) continue; 
			int playerID = std::stoi(idStr.substr(pos + 1));

			float x = std::stof(args[i + 1]);
			float y = std::stof(args[i + 2]);
			float rot = std::stof(args[i + 3]);

			if (playerID >= 0 && playerID < 5)
			{
				if (players[playerID] == nullptr)
				{
					players[playerID] = new Player(playerID, x, y, renderer);
				}
				else
				{
					players[playerID]->setPos(x, y, rot);
				}
			}
			std::cout << "id: " << localplayerID << " x: " << x << " y: " << y << " rot: " << rot << "\n";
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

		for (Player* p : players) {
			if (p != nullptr) {
				p->handleInput(msg);
			}
		}


	}

}


void Game::update(float deltaTime) {

}

void Game::render() {
	SDL_SetRenderDrawColor(renderer, 30, 30, 30, 255);
	SDL_RenderClear(renderer);

	for (Player* p : players) {
		if (p != nullptr) {
			p->render(renderer);
		}
	}


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

void Game::sendPlayerPos() {
	
	
	for (Player* p : players) {
		if (p != nullptr) {
			float x, y;
			p->getPos(x, y);
			float rot = p->getRot();

		}
	}
	std::string msg = "Player: ";
}

void Game::GameLoop() {
	
}

Game::Game() {

	startSDL();
	welcomeScreen();
}
