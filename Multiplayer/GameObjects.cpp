#include "GameObjects.h"

GameObject::GameObject() {

}

GameObject::GameObject(const char* textureName, float x, float y, SDL_Renderer* renderer) : x(x), y(y), rot(0.0f) {
	SDL_Surface* objectSur = IMG_Load(textureName);
	spriteTexture = SDL_CreateTextureFromSurface(renderer, objectSur);

	SDL_FreeSurface(objectSur);

	srcRect = { 0,0,SPRITE_SIZE,SPRITE_SIZE };
	destRect = { (int)x, (int)y,SPRITE_SCREEN_SIZE,SPRITE_SCREEN_SIZE };
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

void GameObject::setSize(int pWidth, int pHeight) // if sprite is non standard size
{
	// The source file
	srcRect.w = pWidth;
	srcRect.h = pHeight;

	// Destination Screen display 
	destRect.w = pWidth * SPRITE_SCREEN_SIZE / SPRITE_SIZE;
	destRect.h = pHeight * SPRITE_SCREEN_SIZE / SPRITE_SIZE;
}

Player::Player(const char* textureName,int id, float x, float y, SDL_Renderer* renderer) : GameObject(textureName,x, y, renderer), playerId(id), barRot(0) {
	
	SDL_Surface* playerSur = IMG_Load(textureName);
	spriteTexture = SDL_CreateTextureFromSurface(renderer, playerSur);

	SDL_FreeSurface(playerSur);

	srcRect = { 0,0,SPRITE_SIZE,SPRITE_SIZE };
	destRect = { (int)x, (int)y,SPRITE_SCREEN_SIZE,SPRITE_SCREEN_SIZE };
	
	SDL_Surface* barSur = IMG_Load("assets/images/playerBarrel.png");
	barTexture = SDL_CreateTextureFromSurface(renderer, barSur);

	SDL_FreeSurface(barSur);

	barSrcRect = { 0,0,SPRITE_SIZE,SPRITE_SIZE };
}

void Player::setMousePos(int mouseXPos, int mouseYPos) {
	mouseX = mouseXPos;
	mouseY = mouseYPos;
}

void Player::render(SDL_Renderer* renderer) {
	SDL_RenderCopyEx(renderer, spriteTexture, &srcRect, &destRect, rot, nullptr, SDL_FLIP_NONE);
	SDL_RenderCopyEx(renderer,  barTexture, &barSrcRect, &destRect, barRot, &barPivot, SDL_FLIP_NONE);

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

void Player::rotateBarrel(){

	float centerX = destRect.x + destRect.w * 0.5f;
	float centerY = destRect.y + destRect.h * 0.5f;

	float dx = mouseX - centerX;
	float dy = mouseY - centerY;

	float angleRad = std::atan2(dy, dx);
	float angleDeg = angleRad * 180.0f / M_PI;
	
	barRot = angleDeg;
	barRot += 90;
}
void Player::setBarRot(float newBarRot) {
	barRot = newBarRot;
}
void Player::setAlive(float Alive) {
	if (Alive == 1) {
		isAlive = true;
	}
	else {
		isAlive = false;
	}
}
Bullet::Bullet(const char* textureName, float x, float y, SDL_Renderer* renderer) {
	SDL_Surface* objectSur = IMG_Load(textureName);
	spriteTexture = SDL_CreateTextureFromSurface(renderer, objectSur);

	SDL_FreeSurface(objectSur);

	srcRect = { 0,0,SPRITE_SIZE,SPRITE_SIZE };
	destRect = { (int)x, (int)y,SPRITE_SCREEN_SIZE,SPRITE_SCREEN_SIZE };
}
void Bullet::render(SDL_Renderer* renderer) {
	SDL_RenderCopyEx(renderer, spriteTexture, &srcRect, &destRect, rot, nullptr, SDL_FLIP_NONE);
}
Tile::Tile(const char* textureName, float x, float y, SDL_Renderer* renderer) {
	SDL_Surface* objectSur = IMG_Load(textureName);
	spriteTexture = SDL_CreateTextureFromSurface(renderer, objectSur);

	SDL_FreeSurface(objectSur);

	srcRect = { 0,0,SPRITE_SIZE,SPRITE_SIZE };
	destRect = { (int)x, (int)y,SPRITE_SCREEN_SIZE,SPRITE_SCREEN_SIZE };
}