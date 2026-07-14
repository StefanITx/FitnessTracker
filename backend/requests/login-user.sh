#!/bin/bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"stefan1@gmail.com","password":"test1234"}'