package be.he2b.scoutpocket.database.repository

import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.model.Section
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun addMember(member: Member)
    suspend fun updateMember(member: Member)
    suspend fun deleteMember(member: Member)
    fun getAllMembers(): Flow<List<Member>>
    fun getMemberById(memberId: String): Flow<Member?>
    fun getMembersBySection(section: Section): Flow<List<Member>>
}