test_db :
	cat `ls resources/db/db_partitions/* | sort` | tee resources/db/test-db.db >/dev/null
