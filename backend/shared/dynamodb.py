import boto3

from shared.config import (
    AWS_REGION,
    LOGIN_TABLE,
    MUSIC_TABLE,
    SUBSCRIPTION_TABLE,
)

dynamodb = boto3.resource("dynamodb", region_name=AWS_REGION)

login_table = dynamodb.Table(LOGIN_TABLE)

music_table = dynamodb.Table(MUSIC_TABLE)

subscription_table = dynamodb.Table(SUBSCRIPTION_TABLE)
