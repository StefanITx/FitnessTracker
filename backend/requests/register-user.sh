#!/bin/bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"stefan","email":"stefan@gmail.com","password":"test1234"}'