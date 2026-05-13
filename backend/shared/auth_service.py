from shared.dynamodb import login_table

from shared.responses import (
    success_response,
    error_response,
)


def login_user(email, password):

    response = login_table.get_item(Key={"email": email})

    user = response.get("Item")

    if not user:
        return error_response("email or password is invalid")

    if user["password"] != password:
        return error_response("email or password is invalid")

    return success_response(
        message="login successful",
        data={"email": user["email"], "user_name": user["user_name"]},
    )


def register_user(email, user_name, password):

    response = login_table.get_item(Key={"email": email})

    existing_user = response.get("Item")

    if existing_user:
        return error_response("The email already exists")

    login_table.put_item(
        Item={"email": email, "user_name": user_name, "password": password}
    )

    return success_response(message="registration successful")
