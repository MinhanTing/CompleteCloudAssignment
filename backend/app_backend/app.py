from flask import Flask, jsonify, request

from shared.auth_service import login_user, register_user
from shared.music_service import query_music
from shared.subscription_service import (
    get_user_subscriptions,
    subscribe_music,
    unsubscribe_music,
)

app = Flask(__name__)


@app.post("/login")
def login():
    body = request.get_json() or {}
    result = login_user(body.get("email"), body.get("password"))
    return jsonify(result), 200 if result["success"] else 401


@app.post("/register")
def register():
    body = request.get_json() or {}
    result = register_user(
        body.get("email"),
        body.get("user_name"),
        body.get("password"),
    )
    return jsonify(result), 200 if result["success"] else 400


@app.get("/music/query")
def music_query():
    results = query_music(
        title=request.args.get("title"),
        artist=request.args.get("artist"),
        year=request.args.get("year"),
        album=request.args.get("album"),
    )
    return jsonify({"success": True, "results": results}), 200


@app.get("/subscriptions")
def subscriptions():
    email = request.args.get("email")

    if not email:
        return jsonify({"success": False, "message": "email is required"}), 400

    results = get_user_subscriptions(email)
    return jsonify({"success": True, "results": results}), 200


@app.post("/subscriptions")
def subscribe():
    body = request.get_json() or {}

    result = subscribe_music(
        email=body.get("email"),
        song=body.get("song"),
    )

    return jsonify(result), 200 if result["success"] else 400


@app.delete("/subscriptions")
def unsubscribe():
    body = request.get_json() or {}

    result = unsubscribe_music(
        email=body.get("email"),
        title=body.get("title"),
    )

    return jsonify(result), 200 if result["success"] else 400


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
