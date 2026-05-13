FROM python:3.14

WORKDIR /app

COPY app_backend/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app_backend/ ./app_backend
COPY shared/ ./shared

EXPOSE 80

CMD ["gunicorn", "-b", "0.0.0.0:80", "app_backend.app:app"]