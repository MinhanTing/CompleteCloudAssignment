from boto3.dynamodb.conditions import (
    Key,
    Attr,
)

from shared.dynamodb import music_table


def query_music(title=None, artist=None, year=None, album=None):

    if not any([title, artist, year, album]):
        return []

    items = []

    if artist and album:
        response = music_table.query(
            IndexName="artist-album-index",
            KeyConditionExpression=Key("artist").eq(artist) & Key("album").eq(album),
        )

        items = response.get("Items", [])

    elif artist:
        response = music_table.query(KeyConditionExpression=Key("artist").eq(artist))

        items = response.get("Items", [])

    elif year:
        response = music_table.query(
            IndexName="year-title-index",
            KeyConditionExpression=Key("year").eq(str(year)),
        )

        items = response.get("Items", [])

    else:
        response = music_table.scan()

        items = response.get("Items", [])


    if title:
        items = [item for item in items if item["title"].lower() == title.lower()]

    if album:
        items = [item for item in items if item["album"].lower() == album.lower()]

    if year:
        items = [item for item in items if str(item["year"]) == str(year)]

    return items
