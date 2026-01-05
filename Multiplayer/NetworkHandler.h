#pragma once
#include <queue>
#include <string>
#include <vector>
#include <mutex>

struct NetMessage {
	std::string cmd;
	std::vector<std::string> args;
};

extern std::queue<NetMessage> netQueue;
extern std::mutex netMutex;