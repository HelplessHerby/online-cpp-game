#include "Game.h"
#include <unordered_map>
#include <unordered_set>

SDL_Renderer* Game::renderer = nullptr;
SDL_Event Game::playerInputEvent;

std::unordered_map<int, Player*> players;
GameObject* background;
PlayerInputState localInput;
PlayerInputState lastSentInput;
float lastSentBarrelRot = 0.0f;

void Game::send(std::string message) {
    if (!message.empty()) {
        std::cout << message << "\n";
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
            players[localplayerID] = new Player("assets/images/testPlayer.png", localplayerID,
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
                players[playerID] = new Player("assets/images/testPlayer.png",playerID, x, y, renderer);
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
    std::string msg = "";

    if (event.type == SDL_KEYDOWN || event.type == SDL_KEYUP) {
        bool down = (event.type == SDL_KEYDOWN);
        switch (event.key.keysym.sym) {
        case SDLK_w: localInput.moveUp = down; break;
        case SDLK_s: localInput.moveDown = down; break;
        case SDLK_a: localInput.turnLeft = down; break;
        case SDLK_d: localInput.turnRight = down; break;
        case SDLK_SPACE: localInput.shooting = down; break;
        }
    }
    auto it = players.find(localplayerID);
    if (it != players.end() && it->second != nullptr) {
        int mouseX, mouseY;
        SDL_GetMouseState(&mouseX, &mouseY);
        it->second->setMousePos(mouseX, mouseY);
        it->second->rotateBarrel();
    }
    
}

void Game::update(float deltaTime,SDL_Event e) {
    input(e);

    auto it = players.find(localplayerID);
    if (it == players.end() || it->second == nullptr) return;

    float barrelRot = it->second->barRot;

    // send if something has changed
    if (localInput != lastSentInput || fabs(barrelRot - lastSentBarrelRot) > 0.01f) {
        std::string msg = "";

        // movement
        msg += std::to_string(localInput.moveUp ? 1 : 0);
        msg += "," + std::to_string(localInput.moveDown ? 1 : 0);
        msg += "," + std::to_string(localInput.turnLeft ? 1 : 0);
        msg += "," + std::to_string(localInput.turnRight ? 1 : 0);
        msg += "," + std::to_string(localInput.shooting ? 1 : 0);

        // rotation
        msg += "," + std::to_string(barrelRot);

        send(msg);

        //last sent state
        lastSentInput = localInput;
        lastSentBarrelRot = barrelRot;
    }
}

void Game::render() {

    background->render(renderer);
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
        //send("Game Created");
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
    background = new GameObject("assets/images/background.png",0,0,renderer);
    background->setSize(800, 600);
    //send("Game Welcome");
}

void Game::sendPlayerData() {

}

void Game::GameLoop() {
}

Game::Game() {
    startSDL();
    gameRunning = true;
}
