#pragma once
#include "SDL.h"
#include <vector>
#include <string>
#include "GameObjects.h"
class Level {
public:
	Level() : levelID(0), width(0), height(0) {}
	int levelID;
	int width;
	int height;
	int tileType;
	int posX,posY;
	int randInt = 0;
	void levelMessage(const std::vector<std::string>& args);
	void renderTiles(SDL_Renderer* renderer);
};

