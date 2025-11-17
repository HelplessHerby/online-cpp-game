#pragma once


#include "SDL.h"
#include <string>
#include <vector>
#include <iostream>
#include <map>

#define GAME_TITLE "Herby's Tanks: Online"
#define SCREEN_WIDTH  800
#define SCREEN_HEIGHT 600
#define SPRITE_SIZE 32
#define SPRITE_SCREEN_SIZE 32
#define ROWS = 18
#define COLUMNS = 25
#define FPS 60

static struct GameData {
	float p1PosX = 0, p1PosY = 0;
	float p2PosX = 0, p2PosY = 0;
	int playerId;
} game_data;

class Game {
private:
	SDL_Rect player1Rect;
	bool gameRunning = false;
	SDL_Window* gameWindow = nullptr;
public:
	Game();
	void startSDL();
	std::vector<std::string> messages;
	void input(SDL_Event& event);
	void send(std::string message);
	void on_receive(std::string message, std::vector<std::string>& args);
	void update();
	void welcomeScreen();
	void GameLoop();
	void render();
	bool isRunning()  const { return gameRunning; }
	void Close();
	static SDL_Renderer* renderer;
	static SDL_Event playerInputEvent;
	std::map<std::string, std::pair<int, int>> playerPositions;
	std::map<SDL_Keycode, bool> keyDown;
};