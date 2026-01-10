## Информационное обеспечение ИС

### Входные данные
- Данные пациента: ФИО, дата рождения, телефон, email, страховые/идентификационные реквизиты (опционально).
- Данные врача: ФИО, специальность, кабинет, лимиты приема.
- Данные расписания: период действия, рабочие часы, перерывы, параметры слотов.
- Данные записи: дата/время, тип приема, источник, комментарий.
- Данные уведомлений: канал, текст/шаблон, статус доставки.

### Выходные данные
- Список доступных слотов (по врачу/дате).
- Список записей пациента/врача.
- Статусы записей (PENDING/CONFIRMED/CANCELLED/COMPLETED).
- Журнал уведомлений и статусы доставки.

### Логическая модель данных (сущности)
Рекомендуемый минимальный набор сущностей:
- **Patients** — пациент.
- **Doctors** — врач.
- **Schedules** — расписание врача на диапазон дат.
- **Appointments** — запись на прием.
- **Notifications** — уведомление (журнал/очередь).

### Физическая модель данных (пример полей)
> Пример под PostgreSQL; при необходимости адаптируется под другую СУБД.

**patients**
- id (UUID, PK)
- full_name (varchar)
- birth_date (date, null)
- phone (varchar, unique)
- email (varchar, unique, null)
- is_archived (boolean)
- created_at (timestamp)
- updated_at (timestamp)

**doctors**
- id (UUID, PK)
- full_name (varchar)
- specialty (varchar, index)
- cabinet_number (varchar, null)
- max_patients_per_day (int)
- is_available (boolean)
- created_at (timestamp)
- updated_at (timestamp)

**schedules**
- id (UUID, PK)
- doctor_id (UUID, FK -> doctors.id)
- date_from (date)
- date_to (date)
- start_time (time)
- end_time (time)
- break_start (time, null)
- break_end (time, null)
- slot_duration (int, minutes)
- max_slots (int)
- created_at (timestamp)
- updated_at (timestamp)

**appointments**
- id (UUID, PK)
- patient_id (UUID, FK -> patients.id)
- doctor_id (UUID, FK -> doctors.id)
- appointment_date (date)
- appointment_time (time)
- status (varchar)
- appointment_type (varchar)
- source (varchar)
- notes (text, null)
- confirmed_at (timestamp, null)
- cancelled_at (timestamp, null)
- created_at (timestamp)
- updated_at (timestamp)

**notifications**
- id (UUID, PK)
- appointment_id (UUID, FK -> appointments.id, null)
- patient_id (UUID, FK -> patients.id)
- notification_type (varchar) // SMS/EMAIL/PUSH
- message_template (varchar, null)
- message_text (text, null)
- status (varchar) // PENDING/SENT/FAILED
- sent_at (timestamp, null)
- delivery_time (bigint, null)
- error_message (text, null)
- created_at (timestamp)