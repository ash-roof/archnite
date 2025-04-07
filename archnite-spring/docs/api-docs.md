# API Documentation

**Base URL:** `/packages/arch`

---

## Get Package by Name

**GET** `/packages/arch/name/{packageName}`

Returns a package by its name.

### Path Parameters

- `packageName` (string) — Name of the package.

### Query Parameters

- `aur` (boolean, optional) — If true, returns only AUR packages, else only
  official packages are returned. If not specified, both package types are returned.

### Response

```json
{
    "id": 1,
    "packageName": "vim",
    "description": "Vi Improved – enhanced vi editor",
    ...
}
```

---

## Search Packages by Keyword

**GET** `/packages/arch/search`

Search for packages by name (fuzzy search supported).

### Query Parameters

- `keyword` (string, required) — Search term. Cannot be blank or null.
- `limit` (integer, optional) — Number of results to return. Default: `10`, max: `50`.
- `aur` (boolean, optional) — If true, returns only AUR packages, else only
  official packages are returned. If not specified, both package types are returned.

### Response

```json
[
    {
        "id": 1,
        "packageName": "vim",
        ...
    }
]
```

---

## Get All Packages (Paginated)

**GET** `/packages/arch`

Retrieve a paginated list of packages.

### Query Parameters

- `page` (integer, optional) — Page index, starting from 0. Default: `0`.
- `size` (integer, optional) — Page size. Default: `50`, min: `5`, max: `150`.
- `aur` (boolean, optional) — If true, returns only AUR packages, else only
  official packages are returned. If not specified, both package types are returned.
- `order` (string, optional) — Sort direction. Either `ASC` or `DESC`. Default: `ASC`.
- `sort` (string, optional) — Field to sort by. Either `PACKAGENAME` or `LASTUPDATE`. Default: `PACKAGENAME`.

### Response

```json
{
    "packages": [
        {
            "id": 1,
            "packageName": "vim",
            ...
        }
    ],
    "currentPage": 0,
    "totalItems": 100,
    "totalPages": 2
}
```

---

## Get Package by ID

**GET** `/packages/arch/{id}`

Returns a package by its database ID.

### Path Parameters

- `id` (integer) — Package ID.

### Response

```json
{
    "id": 1,
    "packageName": "vim",
    ...
}
```

---

## Sample cURL Requests

### Get Package by Name

```bash
curl -X GET "https://api.archnite.omarashraf.dev/packages/arch/name/vim"
```

### Search Packages

```bash
curl -X GET "https://api.archnite.omarashraf.dev/packages/arch/search?keyword=vim&limit=10"
```

### Get All Packages (Paginated)

```bash
curl -X GET "https://api.archnite.omarashraf.dev/packages/arch?page=0&size=20&order=DESC&sort=LASTUPDATE"
```

## ArchPackage Model Schema

All API responses return package objects with the following fields:

| Field          | Type              | Description                                          |
| -------------- | ----------------- | ---------------------------------------------------- |
| `id`           | integer           | Unique identifier of the package                     |
| `architecture` | string OR null    | CPU architecture (e.g., `x86_64`, `any`)             |
| `packageName`  | string            | Name of the package                                  |
| `description`  | string            | Description of the package                           |
| `lastUpdate`   | string (ISO 8601) | Last update timestamp (e.g., `2024-04-07T12:34:56Z`) |
| `url`          | string            | Official URL for the package                         |
| `isAur`        | boolean           | Indicates if the package is from AUR                 |
