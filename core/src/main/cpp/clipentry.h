//
// Created by flicherr on 13.04.2025.
//

#ifndef CAPYPAST_CLIPENTRY_H
#define CAPYPAST_CLIPENTRY_H

#include <cinttypes>
#include <string>
#include <vector>

enum class ClipType : uint8_t {
	TEXT,
	IMAGE
};

struct ClipEntry {
	uint64_t id = 0;
	int64_t timestamp = 0;                  // Время вставки, Unix time
	ClipType type = ClipType::TEXT;
	std::vector<std::string> tags;
	bool isPinned = false;
	bool isProtected = false;
	std::vector<uint8_t> data;              // raw или сжатый контент

	ClipEntry() = default;

	ClipEntry(	uint64_t id_,
				int64_t timestamp_,
				ClipType type_,
				std::vector<std::string> tags_,
				bool pinned_,
				bool protected_,
				std::vector<uint8_t> data_)
		: id(id_),
		timestamp(timestamp_),
		type(type_),
		tags(std::move(tags_)),
		isPinned(pinned_),
		isProtected(protected_),
		data(std::move(data_)) {}
};

#endif //CAPYPAST_CLIPENTRY_H
