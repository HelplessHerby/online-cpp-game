#include "Game.h"
#include <unordered_map>
#include <unordered_set>

enum GameState {
    START_LEVEL,
    DURING_LEVEL,
    END_LEVEL
};

SDL_Renderer* Game::renderer = nullptr;
SDL_Event Game::playerInputEvent;

std::unordered_map<int, Player*> players;
Bullet* bullets[10] = {};
GameObject* background;
PlayerInputState localInput;
PlayerInputState lastSentInput;
float lastSentBarrelRot = 0.0f;
Level* curLevel;
bool isLocalAlive = true;
GameState gs = START_LEVEL;



void Game::send(std::string message) {
    if (!message.empty()) {
        std::cout << message << "\n";
        message += "\n";
        messages.push_back(message);
    }
}

void Game::createObjects() {
    for (int i = 0; i < sizeof(bullets) / sizeof(bullets[0]); i++)
    {
        bullets[i] = new Bullet("assets/images/bullet.png", -100, -100, renderer);
        bullets[i]->setActive(false);
    }
    gs = DURING_LEVEL;
}

void Game::on_receive(std::string cmd, std::vector<std::string>& args) {
    for (std::string& arg : args) {
        arg.erase(std::remove(arg.begin(), arg.end(), '\r'), arg.end());
        arg.erase(std::remove(arg.begin(), arg.end(), '\n'), arg.end());
    }
    if (cmd == "ASSIGN_ID") {
        if (args.empty()) return;

        std::string localPlayerStr = args[0];
        size_t pos = localPlayerStr.find(":");
        if (pos == std::string::npos) {
            std::cerr << "[CLIENT] ERROR: ASSIGN_ID missing colon: "
            << localPlayerStr << std::endl;
            return;
        }
        localplayerID = std::stoi(localPlayerStr.substr(pos + 1));
        std::cout << "[CLIENT] Assigned Player ID: " << localplayerID << std::endl;

            //Spawn Local player
        if (players.find(localplayerID) == players.end()) {
                players[localplayerID] = new Player(
                "assets/images/player.png",
                localplayerID,
                200, 200,
                renderer
            );
        }
        return;
    }
    else if (cmd == "LEVEL_LOAD") {
        curLevel->levelMessage(args);
        std::cout << "[CLIENT] loaded level ID: " << curLevel->levelID
            << " Width: " << curLevel->width
            << " Height: " << curLevel->height << std::endl;
    }
    else if (cmd == "GAME_DATA") {
        std::unordered_set<int> serverIDs;
        size_t i = 0;
        //Each player in chunks of 5 : ID, X, Y, Rotation, Barrel Rotation, Alive status
        while (i < args.size() && args[i] != "BULLETS") {
            std::string idStr = args[i++];
            size_t pos = idStr.find(":");
            if (pos == std::string::npos) continue;
            int playerID = std::stoi(idStr.substr(pos + 1));
            float x = std::stof(args[i++]);
            float y = std::stof(args[i++]);
            float rot = std::stof(args[i++]);
            float barRot = std::stof(args[i++]);
            float isAlive = std::stof(args[i++]);
            serverIDs.insert(playerID);
            
            //Creates Player incase doesn't exist
            if (players.find(playerID) == players.end()) {
                players[playerID] = new Player("assets/images/enemy.png",
                    playerID, (int)x,(int) y, renderer);
            }
            //Update Player
            players[playerID]->setPos(x, y, rot);
            players[playerID]->setBarRot(barRot);
            players[playerID]->setAlive(isAlive);
            //PlayerAlive

            if (playerID == localplayerID) {
                isLocalAlive = (isAlive == 1);
            }
            //Console Output
            
            std::cout << "id: " << playerID
                << " x: " << x
                << " y: " << y
                << " rot: " << rot
                << " bar rot: " << barRot
                << " is Alive: " << isAlive
                << std::endl;
            

        }
        //deletes players
        for (auto it = players.begin(); it != players.end();) {
            if (serverIDs.find(it->first) == serverIDs.end()) {
                delete it->second;
                it = players.erase(it);
            }
            else {
                ++it;
            }
        }
        //Bullets
        if (i < args.size() && args[i] == "BULLETS") {
            i++; //Skip BULLETS
            while (i + 4 < args.size()) {
                int ownerID = std::stoi(args[i++]);
                float bulX = std::stof(args[i++]);
                float bulY = std::stof(args[i++]);
                float angle = std::stof(args[i++]);
                std::string isAliveStr = args[i++];
                std::cout << "Bullet x:" << bulX;
                bool isAlive = (isAliveStr == "true");
                Bullet* bullet = bullets[ownerID];
                if (!isAlive) {
                    bullet->setPos(bulX, bulY, angle);
                    bullet->setActive(false);
                }
                else {
                    bullet->setPos(bulX, bulY, angle);
                    bullet->setActive(true);
                }
            }


        }
        return;
    }
    if (cmd == "SHOOTING") {
        for (size_t i = 0; i + 3 < args.size(); i += 3) {
            std::string idStr = args[i];
            size_t pos = idStr.find(":");
            if (pos == std::string::npos) continue;

            int playerID = std::stoi(idStr.substr(pos + 1));
            float x = std::stof(args[i+1]);
            float y = std::stof(args[i+2]);
            float barRot = std::stof(args[i + 3]);
            std::cout<< "ID: "<< playerID << " x: " << x << " y: " << y;
            for (int i = 0; i < sizeof(bullets) / sizeof(bullets[0]); i++)
            {
                if (!bullets[i]->getActive()) {
                    std::cout << "bullet active";
                    bullets[i]->setPos(x, y, barRot);
                    bullets[i]->setActive(true);
                    break;
                }
            }
        }
    }
    else {
        //std::cout << "Received: " << cmd << std::endl;
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
void Game::levelLoop(SDL_Event e) {
    if (isLocalAlive) {
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
}
void Game::update(float deltaTime,SDL_Event e) {
    switch (gs) {
    case START_LEVEL:
        createObjects();
        break;
    case DURING_LEVEL:
        levelLoop(e);
        break;
    }

}

void Game::render() {

    background->render(renderer);
    for (auto& kv : players) {
        kv.second->render(renderer);
    }
    for (Bullet* bullet : bullets) {

        if (bullet && bullet->getActive()) {
            bullet->render(renderer);
        }

    }
    curLevel->renderTiles(renderer);
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

Game::Game() {
    startSDL();
    welcomeScreen();
    curLevel = new Level();
    gameRunning = true;
}
