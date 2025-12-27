package be.he2b.scoutpocket.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.model.Section

@Dao
interface MemberDao {
    @Insert
    suspend fun insert(member: Member): Long

    @Update
    suspend fun update(member: Member)

    @Delete
    suspend fun delete(member: Member)

    @Query("SELECT * FROM members")
    suspend fun getAllMembers(): List<Member>

    @Query("SELECT * FROM members WHERE section = :section")
    suspend fun getMembersBySection(section: Section): List<Member>

    @Query("SELECT * FROM members WHERE id = :memberId")
    suspend fun getMemberById(memberId: Int): Member?

    @Query("SELECT * FROM members WHERE LOWER(lastName) = LOWER(:lastName) AND LOWER(firstName) = LOWER(:firstName)")
    suspend fun findMemberByName(lastName: String, firstName: String): Member?

}