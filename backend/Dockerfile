FROM node:20-alpine

WORKDIR /app

# SQLite needs python/make for native builds
RUN apk add --no-cache python3 make g++

COPY package.json package-lock.json* ./
RUN npm ci --only=production

COPY src/ ./src/

ENV NODE_ENV=production
ENV PORT=3000

EXPOSE 3000

CMD ["npm", "start"]
