FROM python:3.12-slim
WORKDIR /app
RUN pip install psycopg2-binary pandas openpyxl sqlalchemy
COPY importer/main.py .
# Spreadsheets incluídos na imagem para k8s (sem volume mount disponível).
# Localmente o docker-compose monta-os em runtime.
# O build context tem de ser a raiz de dataBaseService/ para aceder à pasta spreadsheets/.
COPY spreadsheets/ /spreadsheets/
CMD ["python", "main.py"]