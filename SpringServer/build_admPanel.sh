#!/usr/bin/env bash
cd AdminPanel
yarn install
PUBLIC_URL=/admin yarn build
cd ..