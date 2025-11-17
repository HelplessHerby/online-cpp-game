#include "Player.h"

Player::Player(int id, float x, float y, SDL_Renderer* renderer) : playerId(0), x(x), y(y), xVel(0), yVel(0), rot(0)
{
	SDL_Surface* tempSur = IMG_Load("assets/images/testPlayer.png");
	spriteTexture = SDL_CreateTextureFromSurface(renderer, tempSur);

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

	if (xVel > maxSpeed) xVel = maxSpeed;
	if (yVel > maxSpeed) yVel = maxSpeed;
	if (xVel < -maxSpeed) xVel = -maxSpeed;
	if (yVel < -maxSpeed) yVel = -maxSpeed;


	if (abs(xVel) > 0.1f) xVel *= drag; else xVel = 0;
	if (abs(yVel) > 0.1f) yVel *= drag; else yVel = 0;


	x += xVel;
	y += yVel;
}

void Player::render(SDL_Renderer* renderer) {
	SDL_RenderCopyEx(renderer, spriteTexture, &srcRect, &destRect, rot, nullptr, SDL_FLIP_NONE);
}