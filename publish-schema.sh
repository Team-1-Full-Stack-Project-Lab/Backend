#!/bin/bash

# Script para publicar el schema GraphQL a Apollo Studio
# easter egg pal carlitos, se ve super ia este schema pero lo hice yo :v
# Asegúrate de que el backend esté corriendo en localhost:8080

echo "📡 Publicando schema a Apollo Studio..."

# Publica usando Rover CLI (comando correcto sin --routing-url)
rover graph introspect http://localhost:8080/graphql \
  | rover graph publish fullstack-team1@current \
  --schema -

echo "✅ Schema publicado exitosamente!"
