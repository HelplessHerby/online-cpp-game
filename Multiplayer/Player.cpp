#include "Player.h"

Player::Player(int id, float x, float y) : playerId(0), x(x), y(y), xVel(0), yVel(0), rot(0)
{
	SDL_Surface* tempSurface = IMG_Load("assets/images/testPlayer.png");
	spriteTexture = SDL_CreateTextureFromSurface(Game::renderer, tempSurface);
	SDL_FreeSurface(tempSurface);

	srcRect.h = SPRITE_SIZE;
	srcRect.w = SPRITE_SIZE;
	srcRect.x = 0;
	srcRect.y = 0;

	destRect.h = SPRITE_SCREEN_SIZE;
	destRect.w = SPRITE_SCREEN_SIZE;
	destRect.x = x;
	destRect.y = y;

}

