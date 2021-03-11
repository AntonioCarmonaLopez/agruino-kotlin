#!/bin/bash

TOKEN="938242147:AAFo6bm2ggr6QNVETIGFr8hYPlGho29N13I"
ID="519481083"
URL="https://api.telegram.org/bot$TOKEN/sendMessage"

curl -s -X POST $URL -d chat_id=$ID -d text=$1
