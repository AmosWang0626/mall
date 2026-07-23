# 使用 DaoCloud 镜像加速 Docker Hub 拉取
FROM docker.m.daocloud.io/library/node:18-alpine AS build

WORKDIR /app
COPY package.json ./
RUN npm install --registry=https://registry.npmmirror.com

COPY . .
RUN npm run build

# 运行阶段
FROM docker.m.daocloud.io/library/nginx:alpine

# 删除默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 复制自定义 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 复制构建产物
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
