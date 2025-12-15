#include "GameObjects.h"


GameObject::GameObject(float x, float y, SDL_Renderer* renderer) : x(x), y(y), rot(0.0f) {
}

void GameObject::setPos(float setX, float setY, float setRot) {
	destRect.x = setX;
	destRect.y = setY;
	rot = setRot;
}

void GameObject::getPos(float& getX, float& getY, float& getRot){
	getX = x;
	getY = y;
	getRot = rot;
}

void GameObject::render(SDL_Renderer* renderer){
	SDL_RenderCopyEx(renderer, spriteTexture, &srcRect, &destRect, rot, nullptr, SDL_FLIP_NONE);
}




Player::Player(int id, float x, float y, SDL_Renderer* renderer) : GameObject(x, y, renderer), playerId(id) {
	SDL_Surface* tempSur = IMG_Load("assets/images/testPlayer.png");
	spriteTexture = SDL_CreateTextureFromSurface(renderer, tempSur);

	SDL_FreeSurface(tempSur);

	srcRect = { 0,0,SPRITE_SIZE,SPRITE_SIZE };
	destRect = { (int)x, (int)y,SPRITE_SCREEN_SIZE,SPRITE_SCREEN_SIZE };
}



void Player::handleInput(const std::string& input) {
	if (input == "MOVE_UP_DOWN") this->movingForward = true;
	if (input == "MOVE_UP_UP") this->movingForward = false;

	if (input == "MOVE_DOWN_DOWN") this->movingBackward = true;
	if (input == "MOVE_DOWN_UP") this->movingBackward = false;

	if (input == "A_DOWN") this->turningLeft = true;
	if (input == "A_UP") this->turningLeft = false;

	if (input == "D_DOWN") this->turningRight = true;
	if (input == "D_UP") this->turningRight = false;
}