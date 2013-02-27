package com.slobodastudio.forismaticqoutations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

public class ForismaticDatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static final String TAG = ForismaticDatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "ForismaticQuotations.db";
	private static final int DATABASE_VERSION = 1;
	private static Dao<QuotationData, Integer> quotationDao = null;
	
	public ForismaticDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public ForismaticDatabaseHelper(Context context, String dbName, CursorFactory factory, int dbVersion) {
		super(context, dbName, factory, dbVersion);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, QuotationData.class);
			Log.d(TAG, " Databases created successfully");																		//For testing purposes
			//Database test entries
			/*
			Dao<QuotationData, Integer> dao = getQuotationDataDao();
			QuotationData qData = new QuotationData();
			
			qData.setQuotation("Истинный учёный - это мечтатель");
			qData.setAuthor("О. фон Бисмарк");
			qData.setFavourite(true);
			dao.create(qData);
			
			qData.setQuotation("Всё настоящее - мгновение вечности");
			qData.setAuthor("Марк Аврелий");
			qData.setFavourite(false);
			dao.create(qData);
			
			qData.setQuotation("Нет ни одной превосходной души без примеси сумасшествия");
			qData.setAuthor("");
			qData.setFavourite(false);
			dao.create(qData);
			
			qData.setQuotation("Мой жизненный опыт убедил меня, что люди, не имеющие недостатков, имеют очень мало достоинств");
			qData.setAuthor("А. Линкольн ");
			qData.setFavourite(false);
			dao.create(qData);
			
			qData.setQuotation("Женщины способны на всё...., мужчины на всё остальное!");
			qData.setAuthor("Анри Ренье ");
			qData.setFavourite(false);
			dao.create(qData);
			*/
			//Database test entries
		} catch (SQLException e) {
			Log.d(TAG, " Can't create database " + e);																			//For testing purposes
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, QuotationData.class, true);
			Log.d(TAG, " Databases dropped successfully");																		//For testing purposes
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.d(TAG, " Databases drop failed " + e);																			//For testing purposes
			throw new RuntimeException(e);
		}
	}
	
	public Dao<QuotationData, Integer> getQuotationDataDao() throws SQLException {
		if (quotationDao == null) {
			quotationDao = getDao(QuotationData.class);
		}
		return quotationDao;
	}
	
	@Override
	public void close() {
		super.close();
		quotationDao = null;
	}
}