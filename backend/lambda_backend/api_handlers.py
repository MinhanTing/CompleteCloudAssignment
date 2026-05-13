import json

from shared.auth_service import login_user, register_user
from shared.music_service import query_music
from shared.subscription_service import (
    get_user_subscriptions,
    subscribe_music,
    unsubscribe_music,
)


HEADERS = {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers": "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
    "Access-Control-Allow-Methods": "GET,POST,DELETE,OPTIONS"
}


def response(status_code, body):
    return {"statusCode": status_code, "headers": HEADERS, "body": json.dumps(body)}


def parse_body(event):
    return json.loads(event.get("body") or "{}")


def login_handler(event, context):
    body = parse_body(event)

    result = login_user(email=body.get("email"), password=body.get("password"))

    return response(200 if result["success"] else 401, result)


def register_handler(event, context):
    body = parse_body(event)

    result = register_user(
        email=body.get("email"),
        user_name=body.get("user_name"),
        password=body.get("password"),
    )

    return response(200 if result["success"] else 400, result)


def query_music_handler(event, context):
    params = event.get("queryStringParameters") or {}

    results = query_music(
        title=params.get("title"),
        artist=params.get("artist"),
        year=params.get("year"),
        album=params.get("album"),
    )

    return response(200, {"success": True, "results": results})


def get_subscriptions_handler(event, context):
    params = event.get("queryStringParameters") or {}

    email = params.get("email")

    if not email:
        return response(400, {"success": False, "message": "email is required"})

    results = get_user_subscriptions(email)

    return response(200, {"success": True, "results": results})


def subscribe_handler(event, context):
    body = parse_body(event)

    result = subscribe_music(email=body.get("email"), song=body.get("song"))

    return response(200 if result["success"] else 400, result)


def unsubscribe_handler(event, context):
    body = parse_body(event)

    result = unsubscribe_music(email=body.get("email"), title=body.get("title"))

    return response(200 if result["success"] else 400, result)
