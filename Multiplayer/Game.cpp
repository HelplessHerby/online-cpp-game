#include "Game.h"
#include <unordered_map>
#include <unordered_set>

SDL_Renderer* Game::renderer = nullptr;
SDL_Event Game::playerInputEvent;

std::unordered_map<int, Player*> players;

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
            std::cerr << "[Client] ERROR: ASSIGN_ID missing colon: "
                << localPlayerStr << std::endl;
            return;
        }

        localplayerID = std::stoi(localPlayerStr.substr(pos + 1));
        std::cout << "[Client] Assigned Player ID: " << localplayerID << std::endl;

        if (players.find(localplayerID) == players.end()) {
            players[localplayerID] = new Player(localplayerID,
                200 * localplayerID, // x pos
                200 * localplayerID, // y pos
                renderer);
        }
    }

    else if (cmd == "GAME_DATA") {

        std::unordered_set<int> serverIds;

        for (size_t i = 0; i + 3 < args.size(); i += 4) {

            std::string idStr = args[i];
            size_t pos = idStr.find(":");
            if (pos == std::string::npos) continue;

            int playerID = std::stoi(idStr.substr(pos + 1));
            float x = std::stof(args[i + 1]);
            float y = std::stof(args[i + 2]);
            float rot = std::stof(args[i + 3]);

            serverIds.insert(playerID);

            if (players.find(playerID) == players.end()) {
                players[playerID] = new Player(playerID, x, y, renderer);
            }

            players[playerID]->setPos(x, y, rot);

            std::cout << "id: " << playerID
                << " x: " << x
                << " y: " << y
                << " rot: " << rot << std::endl;

            playerPositions[idStr] = { x, y };
        }

        for (auto it = players.begin(); it != players.end(); ) {
            if (serverIds.find(it->first) == serverIds.end()) {
                delete it->second;
                it = players.erase(it);
            }
            else {
                ++it;
            }
        }
    }

    else {
        std::cout << "Received: " << cmd << std::endl;
    }
}

void Game::input(SDL_Event& event) {

    SDL_Keycode key = event.key.keysym.sym;
    bool isDown = (event.type == SDL_KEYDOWN);
    std::string msg;

    if (keyDown[key] != isDown) {
        keyDown[key] = isDown;


        switch (key) {
        case SDLK_w: msg = isDown ? "MOVE_UP_DOWN" : "MOVE_UP_UP"; break;
        case SDLK_s: msg = isDown ? "MOVE_DOWN_DOWN" : "MOVE_DOWN_UP"; break;
        case SDLK_a: msg = isDown ? "TURN_LEFT_DOWN" : "TURN_LEFT_UP"; break;
        case SDLK_d: msg = isDown ? "TURN_RIGHT_DOWN" : "TURN_RIGHT_UP"; break;
        case SDLK_SPACE: msg = isDown ? "SHOOT_DOWN" : "SHOOT_UP"; break;

        }
    }
    int mouseXpos, mouseYpos;
    SDL_GetRelativeMouseState(&mouseXpos, &mouseYpos);
    players[localplayerID]->setMousePos(mouseXpos, mouseYpos);
    if (!msg.empty()) {
        send(msg);
    }

    for (auto& kv : players) {
        kv.second->handleInput(msg);
    }
}

void Game::update(float deltaTime) {

    //local barrel rotations
    auto it = players.find(localplayerID);
    if (it != players.end() && it->second != nullptr) {
        it->second->rotateBarrel();
    }
}

void Game::render() {
    SDL_SetRenderDrawColor(renderer, 30, 30, 30, 255);
    SDL_RenderClear(renderer);

    for (auto& kv : players) {
        kv.second->render(renderer);
    }

    SDL_RenderPresent(renderer);
}

void Game::startSDL() {
    if (SDL_Init(SDL_INIT_EVERYTHING) == 0) {
        gameWindow = SDL_CreateWindow(GAME_TITLE,
            SDL_WINDOWPOS_CENTERED,
            SDL_WINDOWPOS_CENTERED,
            SCREEN_WIDTH,
            SCREEN_HEIGHT,
            0);

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

    for (auto& p : players) {
        delete p.second;
    }
    players.clear();

    SDL_DestroyRenderer(renderer);
    SDL_DestroyWindow(gameWindow);
}

void Game::welcomeScreen() {
    send("Game Welcome");
    gameRunning = true;
}

void Game::sendPlayerPos() {
    for (auto& p : players) {
        float x, y, rot;
        p.second->getPos(x, y, rot);
    }
}

void Game::GameLoop() {
}

Game::Game() {
    startSDL();
    welcomeScreen();
}
