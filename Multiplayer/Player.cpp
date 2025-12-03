#include "Player.h"

Player::Player(int id, float x, float y, SDL_Renderer* renderer) : playerId(0), x(x), y(y), xVel(0), yVel(0), rot(0)
{
	SDL_Surface* tempSur = IMG_Load("assets/images/testPlayer.png");
	spriteTexture = SDL_CreateTextureFromSurface(renderer, tempSur);

	SDL_FreeSurface(tempSur);

	srcRect = { 0,0,SPRITE_SIZE,SPRITE_SIZE };
	destRect = { (int)x, (int)y,SPRITE_SCREEN_SIZE,SPRITE_SCREEN_SIZE };
}

void Player::setPos(float setX, float setY,float setRot) {
	destRect.x = setX;
	destRect.y = setY;
	rot = setRot;
}

void Player::getPos(float& getX, float& getY) {
	getX = x;
	getY = y;
}

void Player::handleInput(const std::string& input) {
	if (input == "MOVE_UP_DOWN") movingForward = true;
	if (input == "MOVE_UP_UP") movingForward = false;

	if (input == "MOVE_DOWN_DOWN") movingBackward = true;
	if (input == "MOVE_DOWN_UP") movingBackward = false;

	if (input == "A_DOWN") turningLeft = true;
	if (input == "A_UP") turningLeft = false;

	if (input == "D_DOWN") turningRight = true;
	if (input == "D_UP") turningRight = false;
}

void Player::update(float deltaTime) {
}

void Player::render(SDL_Renderer* renderer) {
	SDL_RenderCopyEx(renderer, spriteTexture, &srcRect, &destRect, rot, nullptr, SDL_FLIP_NONE);
}