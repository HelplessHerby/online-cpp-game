#include "Level.h"
#include "GameObjects.h"
#include <algorithm>
#include <cctype>
#include <string>

std::vector<Tile*> tiles;


std::string trim(const std::string& s) {
	size_t start = s.find_first_not_of(" \r\n\t");
	if (start == std::string::npos) return "";
	size_t end = s.find_last_not_of(" \r\n\t");
	return s.substr(start, end - start + 1);
}

void Level::levelMessage(const std::vector<std::string>& args) {
	
	if (args.size() < 4) return;
	 this->levelID = std::stoi(args[0]);
	 this->width = std::stoi(args[1]);
	 this->height = std::stoi(args[2]);

	int x = 0, y = 0;
	for (size_t i = 4; i < args.size(); ++i) {
		std::string arg = trim(args[i]);
		if (arg == "LEVEL_END")break;
		if (arg.find("ROW") != std::string::npos) {
			x = 0;
			y++;
			continue;
		}
		if (y >= height)break;
		//Create rows
		this->posX = x * 32.0f;
		this->posY = y * 32.0f;
		std::cout << std::stoi(arg);
		if (std::stoi(arg) == 1) {
			tiles.push_back(new Tile("assets/images/cork.png", posX, posY, Game::renderer));
		}
		x++;
	}
}

