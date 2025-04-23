//
// Created by flicherr on 13.04.2025.
//

#include "historymgr.h"
#include <algorithm>
#include <functional>

HistoryMgr::HistoryMgr() {}

uint64_t HistoryMgr::addEntry(const ClipEntry& entry) {
	ClipEntry copy = entry;
	copy.id = nextId++;
	entries.push_back(copy);
	indexById[copy.id] = entries.size() - 1;
	return copy.id;
}

bool HistoryMgr::removeEntry(uint64_t id) {
	auto it = indexById.find(id);
	if (it == indexById.end()) return false;
	size_t index = it->second;
	entries.erase(entries.begin() + index);
	indexById.clear();
	for (size_t i = 0; i < entries.size(); ++i)
		indexById[entries[i].id] = i;
	return true;
}

std::vector<ClipEntry>
HistoryMgr::getAllEntries(bool includeProtected) const {
	std::vector<ClipEntry> result;
	for (const auto& entry : entries) {
		if (!entry.isProtected || includeProtected || unlocked) {
			result.push_back(entry);
		}
	}
	return result;
}

std::optional<ClipEntry>
HistoryMgr::getEntry(uint64_t id, bool includeProtected) const {
	auto it = indexById.find(id);
	if (it != indexById.end()) {
		const auto& entry = entries[it->second];
		if (!entry.isProtected || includeProtected || unlocked) {
			return entry;
		}
	}
	return std::nullopt;
}

bool HistoryMgr::pinEntry(uint64_t id, bool pin) {
	auto it = indexById.find(id);
	if (it == indexById.end()) return false;
	entries[it->second].isPinned = pin;
	return true;
}

bool HistoryMgr::setProtected(uint64_t id, bool isProtected) {
	auto it = indexById.find(id);
	if (it == indexById.end()) return false;
	entries[it->second].isProtected = isProtected;
	return true;
}

// Простейший поиск по вхождению
std::vector<ClipEntry>
HistoryMgr::search(const std::string& query, bool includeProtected) const {
	std::vector<ClipEntry> result;
	for (const auto& entry : entries) {
		if (entry.isProtected && !includeProtected && !unlocked) continue;

		if (entry.type == ClipType::TEXT) {
			std::string text(entry.data.begin(), entry.data.end());
			if (text.find(query) != std::string::npos) {
				result.push_back(entry);
			}
		}
	}
	return result;
}

std::vector<ClipEntry>
HistoryMgr::filterByTag(const std::string& tag, bool includeProtected) const {
	std::vector<ClipEntry> result;
	for (const auto& entry : entries) {
		if (entry.isProtected && !includeProtected && !unlocked) continue;

		if (std::find(entry.tags.begin(), entry.tags.end(), tag) != entry.tags.end()) {
			result.push_back(entry);
		}
	}
	return result;
}

std::vector<ClipEntry>
HistoryMgr::getSorted(SortMode mode, bool includeProtected) const {
	auto result = getAllEntries(includeProtected);

	switch (mode) {
		case SortMode::TIME_ASC:
			std::sort(result.begin(), result.end(),
					  [](auto& a, auto& b) {
				return a.timestamp < b.timestamp;
			});
			break;
		case SortMode::TIME_DESC:
			std::sort(result.begin(), result.end(),
					  [](auto& a, auto& b) {
				return a.timestamp > b.timestamp;
			});
			break;
		case SortMode::PINNED_FIRST:
			std::sort(result.begin(), result.end(),
					  [](auto& a, auto& b) {
				return a.isPinned && !b.isPinned;
			});
			break;
	}

	return result;
}

// Простая заглушка для PIN — позже можно подключить bcrypt/sha256
void HistoryMgr::setPinCodeHash(const std::string& hash) {
	storedPinHash = hash;
}

bool HistoryMgr::verifyPinCode(const std::string& pin) const {
	return pin == storedPinHash; // заменить на сравнение хеша
}

bool HistoryMgr::unlockWithPin(const std::string& pin) {
	if (verifyPinCode(pin)) {
		unlocked = true;
		return true;
	}
	return false;
}

bool HistoryMgr::isUnlocked() const {
	return unlocked;
}
