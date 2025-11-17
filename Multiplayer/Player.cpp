#include "Player.h"

Player::Player(int id, float x, float y) : playerId(0), x(x), y(y), xVel(0), yVel(0), rot(0)
{
	SDL_Surface* tempSur = IMG_Load("assets/images/testPlayer.png");
	spriteTexture = SDL_CreateTextureFromSurface(nullptr, tempSur);

	SDL_FreeSurface(tempSur);

	srcRect = { 0,0,SPRITE_SIZE,SPRITE_SIZE };
	destRect = { (int)x, (int)y,SPRITE_SCREEN_SIZE,SPRITE_SCREEN_SIZE };
}

void Player::setPos(float setX, float setY) {
	x = setX;
	y = setY;
}

void Player::getPos(float& getX, float& getY) {
	getX = x;
	getY = y;
}

void Player::handleInput(const std::string& input) {
	if (input == "W_DOWN") movingForward = true;
	if (input == "W_UP") movingForward = false;

	if (input == "S_DOWN") movingBackward = true;
	if (input == "S_UP") movingBackward = false;

	if (input == "A_DOWN") turningLeft = true;
	if (input == "A_UP") turningLeft = false;

	if (input == "D_DOWN") turningRight = true;
	if (input == "D_UP") turningRight = false;
}

void Player::update(float deltaTime) {
	float rad = rot * (M_PI / 180.0f);

	if (turningLeft) rot -= rotationSpeed * deltaTime;
	if (turningRight) rot += rotationSpeed * deltaTime;

	if (movingForward) {
		xVel += std::sin(rad) * acceleration * deltaTime;
		yVel -= std::cos(rad) * acceleration * deltaTime;
	}
	if (movingBackward) {
		xVel -= sin(rad) * acceleration * deltaTime;
		yVel += cos(rad) * acceleration * deltaTime;
	}

	float speed = sqrt(xVel * xVel + yVel * yVel);

	if (speed > maxSpeed) {
		float tempSpeed = maxSpeed / speed;
		xVel *= tempSpeed;
		yVel *= tempSpeed;
	}

	xVel *= drag;
	yVel *= drag;


	x += xVel * deltaTime;
	y += yVel * deltaTime;

	destRect.x = (int)x;
	destRect.y = (int)y;
}

void Player::render(SDL_Renderer* renderer) {
	SDL_RenderCopyEx(renderer, spriteTexture, &srcRect, &destRect, rot, nullptr, SDL_FLIP_NONE);
}