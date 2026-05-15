from boto3.dynamodb.conditions import Key
from shared.dynamodb import subscription_table
from shared.responses import success_response, error_response

def get_user_subscriptions(email):
    response = subscription_table.query(KeyConditionExpression=Key("email").eq(email))
    return response.get("Items", [])

def subscribe_music(email, song):
    artist_title_year = song["artist"] + "#" + song["title"] + "#" + song["year"]
    existing = subscription_table.get_item(Key={"email": email, "title": artist_title_year})
    if "Item" in existing:
        return error_response("subscription already exists")
    subscription_table.put_item(Item={"email": email, "title": artist_title_year, "display_title": song["title"], "artist": song["artist"], "year": song["year"], "album": song["album"], "img_url": song.get("img_url", "")})
    return success_response(message="subscription added")

def unsubscribe_music(email, title):
    subscription_table.delete_item(Key={"email": email, "title": title})
    return success_response(message="subscription removed")
