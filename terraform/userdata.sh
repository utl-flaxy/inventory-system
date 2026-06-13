#!/bin/bash

# パッケージ更新
apt update -y

# Dockerインストール
apt install -y docker.io

# Gitインストール
apt install -y git

# Docker起動
systemctl enable docker
systemctl start docker

# docker compose plugin
apt install -y docker-compose-v2

# リポジトリ取得
cd /home/ubuntu

git clone https://github.com/utl-flaxy/inventory-management-system.git

# 起動
cd inventory-management-system

docker compose up -d