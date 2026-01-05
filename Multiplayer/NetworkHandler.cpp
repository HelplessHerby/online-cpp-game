#include "NetworkHandler.h"

std::queue<NetMessage> netQueue;
std::mutex netMutex;