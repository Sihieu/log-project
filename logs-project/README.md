# 1. Cài đặt posgresql (trên docker)

# 2. Tạo table logs giả sử là: 
- Cấu trúc bảng như sau:

```
CREATE TABLE storage_event (
    id VARCHAR(64) PRIMARY KEY,
    target_type TEXT NOT NULL,
    target_id VARCHAR(64) NOT NULL,
    subject_type TEXT NOT NULL,
    subject_id VARCHAR(64) NOT NULL,
    action TEXT NOT NULL,
    data JSONB,
    correlation_id VARCHAR(64),
    created_at BIGINT NOT NULL DEFAULT 0
);
```

# 3. Viết chương trình Java đọc log và gửi vào OpenSearch

Viết chương trình Java thực hiện các bước sau:

- Kết nối tới PostgreSQL.
- Truy vấn bảng `storage_event` để lấy log mới.
- Gửi log tới OpenSearch sử dụng OpenSearch Java Client.

⚠️ Ghi chú: Không bắt buộc dùng Spring Boot. Java thuần + OpenSearch client sẽ nhẹ và nhanh hơn.
   
# 4. Xây dựng chương trình java để tìm kiếm nội dung log. Kết quả trả về json.
   
# 5. Xây dựng dashboard để show kết quả logs. Kết quả trả về dạng json,

