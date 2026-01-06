#include "LevelSystem.h"
int levelSystem::getTileContent(int level, int x, int y)
{
	level = level - 1; // start at 0 in array
	return levelMaps[level][y][x];
}


void levelSystem::setTileContent(int level, int x, int y, int newObject)
{
	levelMaps[level][y][x] = newObject;
}