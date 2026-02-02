package be.he2b.scoutpocket.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.model.Section
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: Member)

    @Update
    suspend fun update(member: Member)

    @Delete
    suspend fun delete(member: Member)

    // TODO: Find a way to sort by section and for each section, members are sorted by lastName et firstName
    @Query("SELECT * FROM members")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE section = :section ORDER BY lastName ASC, firstName ASC")
    fun getMembersBySection(section: Section): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :memberId")
    fun getMemberById(memberId: String): Flow<Member?>

    @Query("SELECT * FROM members WHERE LOWER(lastName) = LOWER(:lastName) AND LOWER(firstName) = LOWER(:firstName)")
    suspend fun findMemberByName(lastName: String, firstName: String): Member?

}