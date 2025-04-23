//
// Created by flicherr on 13.04.2025.
//

#ifndef CAPYPAST_HISTORYMGR_H
#define CAPYPAST_HISTORYMGR_H

#include "clipentry.h"
#include <optional>
#include <string>
#include <vector>
#include <unordered_map>

enum class SortMode {
	TIME_ASC,
	TIME_DESC,
	PINNED_FIRST
};

class HistoryMgr {
public:
	HistoryMgr();

	uint64_t 	addEntry(const ClipEntry& entry);
	bool 		removeEntry(uint64_t id);

	std::vector<ClipEntry>
	getAllEntries(bool includeProtected = false) const;

	std::optional<ClipEntry>
	getEntry(uint64_t id, bool includeProtected = false) const;

	bool pinEntry(uint64_t id, bool pin);
	bool setProtected(uint64_t id, bool isProtected);

	std::vector<ClipEntry>
	search(const std::string& query, bool includeProtected = false) const;

	std::vector<ClipEntry>
	filterByTag(const std::string& tag, bool includeProtected = false) const;

	std::vector<ClipEntry>
	getSorted(SortMode mode, bool includeProtected = false) const;

	void setPinCodeHash(const std::string& hash);
	bool verifyPinCode(const std::string& pin) const;
	bool unlockWithPin(const std::string& pin);
	bool isUnlocked() const;

private:
	std::vector<ClipEntry> entries;
	std::unordered_map<uint64_t, size_t> indexById;
	std::string storedPinHash;
	bool unlocked = false;

	uint64_t nextId = 1;
};

#endif //CAPYPAST_HISTORYMGR_H
